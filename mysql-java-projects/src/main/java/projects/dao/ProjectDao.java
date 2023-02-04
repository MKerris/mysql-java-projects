package projects.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

	private static final String CATEGORY_TABLE  = "category";
	private static final String MATERIAL_TABLE  = "material";
	private static final String PROJECT_TABLE  = "project";
	private static final String PROJECT_CATEGORY_TABLE  = "project_category";
	private static final String STEP_TABLE  = "step";
	
	
	// Switch case 1 - Allow user to add a project to the database and collect details for new project
	public Project insertProject(Project project) {
	
		// @formatter:off
		String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		
		try(Connection conn = DbConnection.getConnection()) {				// Initiate connection with DB. If successful, try running SQL statement. If fail, throw exception
			
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);							// Set values submitted by user to project for writing to the table
				return project;
				
				
			} catch(Exception e) {
				rollbackTransaction(conn);									// If SQL statement fails, roll back transaction
				throw new DbException(e);
			}
			
		} catch(SQLException e) {
			throw new DbException(e);
		}
		
	}


	// Switch case 2 - List all projects in projects table
	public List<Project> fetchAllProjects() {
		
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

		try(Connection conn = DbConnection.getConnection()) {
			
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				try(ResultSet rs = stmt.executeQuery(sql)) {
					
					List<Project> projects = new LinkedList<>();
					
					while (rs.next()) {
						projects.add(extract(rs, Project.class));			// Add all projects from table to list
					}

				return projects;
				
				}
					
			} catch(Exception e) {
				rollbackTransaction(conn);									// If PreparedStatement or ResultSet fails, roll back transaction and throw exception
				throw new DbException(e);
			}
				
		} catch(SQLException e) {											// If connection fails, throw SQLException
			throw new DbException(e);
		}
		
	}


	// Switch case 3 - Allow user to select a specific project to work with
	public Optional<Project> fetchProjectByID(Integer projectId) {
		
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()) {
			
			startTransaction(conn);
			
			try {
				Project project = null;										// Declare project variable and set to null
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);		// Add projectId as parameter to SQL statement
					
					try(ResultSet rs = stmt.executeQuery()) {
						if(rs.next()) {										// If can be used instead of while when expecting a single value returned
							project = extract(rs, Project.class);			// Add project details to project for selected projectId
						}
					}
				}
				
				if(Objects.nonNull(project)) {								// Obtain Materials, Steps, and Categories details for selected projectId
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}

				commitTransaction(conn);
				
				return Optional.ofNullable(project);
					
			} catch(Exception e) {
				rollbackTransaction(conn);									// If PreparedStatement or ResultSet fails, roll back transaction and throw exception
				throw new DbException(e);
			}
				
		} catch(SQLException e) {											// If connection fails, throw SQLException
			throw new DbException(e);
		}

	}


	// Switch case 3 - Allow user to select a specific project to work with and return related Categories
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {

		// @formatter:off
		String sql = ""
				+ "SELECT c.* FROM " + CATEGORY_TABLE + " c "				// Need to join on project category table to link project_id with category_id
				+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
				+ "WHERE project_id = ?";
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);				// Replace ? parameter in SQL with projectId from user selection

			try(ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();				// LinkedList allows for dynamically sized list with values kept in order
				
				while(rs.next()) {
					categories.add(extract(rs, Category.class));			// Add all returned values to List
				}
				
				return categories;

			}
		}
	}


	// Switch case 3 - Allow user to select a specific project to work with and return related Steps
	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {

		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);				// Replace ? parameter in SQL with projectId from user selection
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();						// LinkedList allows for dynamically sized list with values kept in order
				
				while(rs.next()) {
					steps.add(extract(rs, Step.class));						// Add all returned values to List
				}
				
				return steps;

			}
		}
	}


	// Switch case 3 - Allow user to select a specific project to work with and return related Materials
	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {

		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);				// Replace ? parameter in SQL with projectId from user selection
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();				// LinkedList allows for dynamically sized list with values kept in order

				while(rs.next()) {
					materials.add(extract(rs, Material.class));				// Add all returned values to List
				}
				
				return materials;

			}
		}
	}


	// Switch case 4 - Accepts updated project details and updates the database. Returns database success/fail.
	public boolean modifyProjectDetails(Project updatedProj) {

		// @formatter:off
		String sql = ""
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";
		// @formatter:on
		
		try(Connection conn = DbConnection.getConnection()) {				// Initiate connection with DB. If successful, try running SQL statement. If fail, throw exception
			
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				setParameter(stmt, 1, updatedProj.getProjectName(), String.class);
				setParameter(stmt, 2, updatedProj.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, updatedProj.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, updatedProj.getDifficulty(), Integer.class);
				setParameter(stmt, 5, updatedProj.getNotes(), String.class);
				setParameter(stmt, 6, updatedProj.getProjectId(), Integer.class);
				
				boolean updated = stmt.executeUpdate() == 1;				// If update to database succeeds, will return 1 (true)
				commitTransaction(conn);
				
				return updated;												// Send update success (1/true) or fail (0/false) back to service layer
				
				
			} catch(Exception e) {
				rollbackTransaction(conn);									// If SQL statement fails, roll back transaction
				throw new DbException(e);
			}
			
		} catch(SQLException e) {
			throw new DbException(e);
		}
	}


	// Switch case 5 - Delete selected project from table
	public boolean deleteProject(Integer projectId) {

		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()) {				// Initiate connection with DB. If successful, try running SQL statement. If fail, throw exception
			
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				setParameter(stmt, 1, projectId, Integer.class);
				
				boolean updated = stmt.executeUpdate() == 1;				// If update to database succeeds, will return 1 (true)
				commitTransaction(conn);
				
				return updated;												// Send update success (1/true) or fail (0/false) back to service layer
				
				
			} catch(Exception e) {
				rollbackTransaction(conn);									// If SQL statement fails, roll back transaction
				throw new DbException(e);
			}
			
		} catch(SQLException e) {
			throw new DbException(e);
		}
		
	}

	
}