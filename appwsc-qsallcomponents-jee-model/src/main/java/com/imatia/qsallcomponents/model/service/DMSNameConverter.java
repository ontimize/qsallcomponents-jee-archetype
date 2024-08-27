package com.imatia.qsallcomponents.model.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.server.dao.common.INameConvention;
import com.ontimize.jee.server.dms.model.OFile;
import com.ontimize.jee.server.dms.rest.IDMSNameConverter;

@Service("DMSNameConverter")
public class DMSNameConverter implements IDMSNameConverter {

	@Override
	public Object getFileIdColumn() {
		return DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE;
	}

	@Override
	public Object getFileNameColumn() {
		return DMSNaming.DOCUMENT_FILE_NAME;
	}

	@Override
	public Object getFileSizeColumn() {
		return DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE;
	}

	@Override
	public Object getCategoryIdColumn() {
		return DMSNaming.CATEGORY_ID_CATEGORY;
	}

	@Override
	public Object getCategoryNameColumn() {
		return DMSNaming.CATEGORY_CATEGORY_NAME;
	}

	@Override
	public OFile createOFile(Map<?, ?> params) {
		OFile file = new OFile();
		file.setId((Integer) params.get(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE));
		file.setName((String) params.get(DMSNaming.DOCUMENT_FILE_NAME));
		file.setType((String) params.get(DMSNaming.DOCUMENT_FILE_TYPE));
		file.setSize((Integer) params.get(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE));
		file.setCreationDate(((Date) params.get(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE)).getTime());
		file.setDirectory(false);
		return file;
	}

	@Override
	public List<?> getFileColumns(List<?> columns) {
		return Arrays.asList(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE, DMSNaming.DOCUMENT_FILE_NAME, DMSNaming.DOCUMENT_FILE_TYPE, DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE,
				DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE);
	}

	@Override
	public List<?> getCategoryColumns(List<?> columns) {
		return Arrays.asList(DMSNaming.CATEGORY_ID_CATEGORY, DMSNaming.CATEGORY_CATEGORY_NAME, DMSNaming.CATEGORY_ID_CATEGORY_PARENT);
	}

	@Override
	public INameConvention getNameConvention() {
		// TODO Auto-generated method stub
		return null;
	}

}
