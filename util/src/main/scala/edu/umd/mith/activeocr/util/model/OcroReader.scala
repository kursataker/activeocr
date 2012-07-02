/*
 * #%L
 * Active OCR Utilities
 * %%
 * Copyright (C) 2011 - 2012 Maryland Institute for Technology in the Humanities
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package edu.umd.mith.activeocr.util.model

import scala.io._
import scala.util.control.Breaks._
import scala.xml.MetaData
import scala.xml.pull._

object OcroReader {
  def main(args: Array[String]) {
    val filename = "/luxmundi2.html"
    val source = Source.fromInputStream(
      getClass.getResourceAsStream(filename)
    )
    val reader = new XMLEventReader(source)
    while (reader.hasNext) {
      val event = reader.next
      event match {
        case EvElemStart(_, "div", attrs, _) => {
          val clss = attrs.asAttrMap.getOrElse("class", "")
          if (clss == "ocr_page") {
            val page = makeNewPageZone(reader, attrs)
            println(page)
          }
        }
        case EvElemStart(_, "title", _, _) => { eatTitle(reader) }
        case EvElemStart(_, "body"|"head"|"html"|"meta", _, _) => { }
        case EvElemEnd(_, "body"|"head"|"html"|"meta") => { }
        case EvText(text) => { assume(text.trim.isEmpty) }
      }
    }
    source.close
  }

  def eatTitle(reader: XMLEventReader) = {
    breakable {
      while (reader.hasNext) {
        val event = reader.next
        event match {
          case EvElemEnd(_, "title") => { break }
          case EvText(text) => { /* ignore */ }
        }
      }
    }
  }

  def makeNewPageZone(reader: XMLEventReader, attributes: MetaData): Page = {
    var zone = new Zone(IndexedSeq[Line]())
    breakable {
      while (reader.hasNext) {
        val event = reader.next
        event match {
          case EvElemStart(_, "p", _, _) => { /* ignore */ }
          case EvElemStart(_, "span", attrs, _) => {
            val clss = attrs.asAttrMap.getOrElse("class", "")
            if (clss == "ocr_line") {
              zone = zone.addChild(makeNewLine(reader, attrs))
            }
          }
          case EvElemEnd(_, "div") => { break }
          case EvElemEnd(_, "p") => { /* ignore */ }
          case EvText(text) => { assume(text.trim.isEmpty) }
        }
      }
    }
    var page = new Page(IndexedSeq[Zone]())
    page = page.addChild(zone)
    page
  }

  def makeNewLine(reader: XMLEventReader, attributes: MetaData): TermLine = {
    val title = attributes.asAttrMap.getOrElse("title", "")
    val (x, y, w, h) = unpackDimensions(title)
    var tmpText = ""
    breakable {
      while (reader.hasNext) {
        val event = reader.next
        event match {
          case EvComment(_) => { /* HTML entities */ }
          case EvElemEnd(_, "span") => { break }
          case EvText(text) => { tmpText = text }
        }
      }
    }
    val line = new TermLine(tmpText, x, y, w, h)
    line
  }

  def unpackDimensions(title: String): (Int, Int, Int, Int) = {
    val Re = ".*bbox (\\d+) (\\d+) (\\d+) (\\d+)".r
    val Re(x0, y0, x1, y1) = title
    val x = x0.toInt; val y = y0.toInt
    val w = x1.toInt - x0.toInt
    val h = y1.toInt - y0.toInt
    (x, y, w, h)
  }
}