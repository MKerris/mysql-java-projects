package projects.service;

import java.util.*;
import projects.dao.ProjectDao;
import projects.entity.Project;

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
		
		return projectDao.fetchProjectByID(projectId).orElseThrow( () -> new NoSuchElementException("Project with project ID = " + projectId + " does not exist."));
		
	}

	
}
