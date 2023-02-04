package projects.service;

import java.util.*;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {

	private ProjectDao projectDao = new ProjectDao();
	

	// Used for switch case 1 - Allow user to add a project to the database and collect details for new project
	public Project addProject(Project project) {
		
		return projectDao.insertProject(project);
	}


	// Used for switch case 2 - List all projects in projects table
	public List<Project> fetchAllProjects() {
		
		return projectDao.fetchAllProjects();
	}


	// Used for switch case 3 - Allow user to select a specific project to work with
	public Project fetchProjectByID(Integer projectId) {
		
		// Returns selected project details or throws exception of project not found
		return projectDao.fetchProjectByID(projectId).orElseThrow( () -> new NoSuchElementException("Project with project ID = " + projectId + " does not exist."));
		
	}

	
	// Used for switch case 4 - Allow user to update existing project details
	public void modifyProjectDetails(Project updatedProj) {
		
		// If update fails, throw exception. Success will return value
		if(!projectDao.modifyProjectDetails(updatedProj))
			throw new DbException("Project with ID " + updatedProj.getProjectId() + " does not exist.");
		
	}


	// Used for switch case 5 - Allow user to delete a specified project 
	public void deleteProject(Integer projectId) {
		
		// If delete fails, throw exception. Success will return value
		if(!projectDao.deleteProject(projectId))
			throw new DbException("Project with ID " + projectId + " does not exist.");
		
	}

	
}
