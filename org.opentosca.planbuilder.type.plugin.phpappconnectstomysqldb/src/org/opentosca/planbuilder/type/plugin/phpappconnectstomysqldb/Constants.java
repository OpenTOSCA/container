package org.opentosca.planbuilder.type.plugin.phpappconnectstomysqldb;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 *
 */
public final class Constants {

	public static final String pluginId = "OpenTOSCA PlanBuilder Type Plugin PhpApp connects to MySQL Database";

	public static final QName phpAppType = new QName("http://opentosca.org/types/declarative", "PhpApplication");
	public static final QName phpAppConnectsToMySqlType = new QName("http://opentosca.org/types/declarative", "PhpAppConnectsToMySQLDB");
	public static final QName mySqlDbType = new QName("http://opentosca.org/types/declarative", "MySQLDatabase");
	public static final QName mySqlServerType = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaSpecificTypes", "MySQL");
	
	

	public static final List<String> placeholderNames = Arrays.asList("DBAddressPlaceHolder", "DBUserPlaceHolder", "DBPasswordPlaceHolder", "DBNamePlaceHolder");
}
