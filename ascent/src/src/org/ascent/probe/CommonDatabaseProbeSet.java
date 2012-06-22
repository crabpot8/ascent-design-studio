 /**************************************************************************
 * Copyright 2008 Jules White                                              *
 *                                                                         *
 * Licensed under the Apache License, Version 2.0 (the "License");         *
 * you may not use this file except in compliance with the License.        *
 * You may obtain a copy of the License at                                 *
 *                                                                         *
 * http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                         *
 * Unless required by applicable law or agreed to in writing, software     *
 * distributed under the License is distributed on an "AS IS" BASIS,       *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.*
 * See the License for the specific language governing permissions and     *
 * limitations under the License.                                          *
 **************************************************************************/


package org.ascent.probe;


public class CommonDatabaseProbeSet extends ProbeSet {

	public CommonDatabaseProbeSet() {
		super();
		getProbes().add(new ClassProbe("DB2","com.ibm.db2.jdbc.app.DB2Driver"));
		getProbes().add(new ClassProbe("MySQL","com.mysql.jdbc.Driver"));
		getProbes().add(new ClassProbe("Oracle","oracle.jdbc.driver.OracleDriver"));
		getProbes().add(new ClassProbe("HSQL","org.hsqldb.jdbcDriver"));
		getProbes().add(new ClassProbe("MSSQL","com.microsoft.jdbc.sqlserver.SQLServerDriver"));
	}

}
