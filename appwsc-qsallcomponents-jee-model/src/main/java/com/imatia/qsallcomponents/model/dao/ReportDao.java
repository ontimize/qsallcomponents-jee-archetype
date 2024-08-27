package com.imatia.qsallcomponents.model.dao;

import com.ontimize.jee.report.server.dao.IReportDao;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;

@Lazy
@Repository(value = "ReportDao")
@ConfigurationFile(configurationFile = "base-dao/ReportDao.xml", configurationFilePlaceholder = "base-dao/placeholders.properties")
public class ReportDao extends OntimizeJdbcDaoSupport implements IReportDao {

	public static final String	ATTR_ID						= "REPORTID";
	public static final String	ATTR_NAME					= "REPORTNAME";
	public static final String	ATTR_DESCRIPTION			= "REPORTDESCRIPTION";
	public static final String	ATTR_REPORT_TYPE			= "REPORTTYPE";
	public static final String	ATTR_REPORT_FILENAME		= "REPORTFILENAME";
	public static final String	ATTR_ZIP					= "REPORTZIP";
	public static final String	ATTR_COMPILED				= "REPORTCOMPILED";

}
