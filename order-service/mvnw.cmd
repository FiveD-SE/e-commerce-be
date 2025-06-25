@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM   http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM
@REM Apache Maven Wrapper startup batch script
@REM

@setlocal

set MAVEN_PROJECTBASEDIR=%~dp0

if "%MAVEN_WRAPPER_LAUNCHER%"=="" set MAVEN_WRAPPER_LAUNCHER=mvnw
if "%MAVEN_WRAPPER_JAR%"=="" set MAVEN_WRAPPER_JAR=.mvn/wrapper/maven-wrapper.jar
if "%MAVEN_WRAPPER_PROPERTIES%"=="" set MAVEN_WRAPPER_PROPERTIES=.mvn/wrapper/maven-wrapper.properties

if not exist "%MAVEN_PROJECTBASEDIR%%MAVEN_WRAPPER_JAR%" (
  echo Could not find %MAVEN_WRAPPER_JAR%, please run 'mvn -N io.takari:maven:wrapper' to generate it.
  exit /b 1
)

"%JAVA_HOME%\bin\java" -jar "%MAVEN_PROJECTBASEDIR%%MAVEN_WRAPPER_JAR%" %*
@endlocal 