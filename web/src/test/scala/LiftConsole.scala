/*
 * #%L
 * Active OCR Web Application
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
import _root_.bootstrap.liftweb.Boot
import _root_.scala.tools.nsc.MainGenericRunner

object LiftConsole {
  def main(args : Array[String]) {
    // Instantiate your project's Boot file
    val b = new Boot()
    // Boot your project
    b.boot
    // Now run the MainGenericRunner to get your repl
    MainGenericRunner.main(args)
    // After the repl exits, then exit the scala script
    sys.exit(0)
  }
}
