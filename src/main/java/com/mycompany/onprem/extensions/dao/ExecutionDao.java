package com.workato.onprem.extensions.dao;

import java.util.List;

import com.workato.onprem.extensions.model.Execution;

public interface ExecutionDao {
	List<Execution> findAll();

	Execution findById(String id);

	boolean createExecution(Execution execution);

	boolean startExecution(String id);
	
	boolean stopExecution(String id);

	boolean deleteExecution(Execution execution);
}
