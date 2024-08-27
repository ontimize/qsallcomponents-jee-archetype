package com.imatia.qsallcomponents.api.services;

import java.util.List;
import java.util.Map;

import com.ontimize.jee.common.db.AdvancedEntityResult;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;

public interface IBranchService {

	// ---- BRANCHES ----

	public EntityResult branchQuery(Map<String, Object> keysValues, List<String> attributes) throws OntimizeJEERuntimeException;
	
	public EntityResult branchQuery(Map<String, Object> keysValues, List<String> attributes, List<?> sort) throws OntimizeJEERuntimeException;

	public AdvancedEntityResult branchPaginationQuery(Map<?, ?> keysValues, List<?> attributes, int recordNumber, int startIndex, List<?> orderBy)
			throws OntimizeJEERuntimeException;

	public EntityResult branchInsert(Map<String, Object> attributes) throws OntimizeJEERuntimeException;

	public EntityResult branchUpdate(Map<String, Object> attributes, Map<String, Object> keyValues) throws OntimizeJEERuntimeException;

	public EntityResult branchDelete(Map<String, Object> keyValues) throws OntimizeJEERuntimeException;

	// ---- ACCOUNTS ----

	public EntityResult accountQuery(Map<String, Object> keysValues, List<String> attributes) throws OntimizeJEERuntimeException;

	public AdvancedEntityResult accountPaginationQuery(Map<?, ?> keysValues, List<?> attributes, int recordNumber, int startIndex, List<?> orderBy)
			throws OntimizeJEERuntimeException;

	public EntityResult accountInsert(Map<String, Object> attributes) throws OntimizeJEERuntimeException;

	public EntityResult accountUpdate(Map<String, Object> attributes, Map<String, Object> keyValues) throws OntimizeJEERuntimeException;

	public EntityResult accountDelete(Map<String, Object> keyValues) throws OntimizeJEERuntimeException;

	// ---- ACCOUNTS CONCEPTS ----
	public EntityResult accountConceptsQuery(Map<String, Object> keysValues, List<String> attributes) throws OntimizeJEERuntimeException;

	// ---- ACCOUNTS MOVEMENTTYPES ----
	public EntityResult accountMovementTypesQuery(Map<String, Object> keysValues, List<String> attributes) throws OntimizeJEERuntimeException;

	// ---- ACCOUNT TYPES ----
	public EntityResult accountTypeAggregateQuery(Map<String, Object> keysValues, List<String> attributes) throws OntimizeJEERuntimeException;

}
