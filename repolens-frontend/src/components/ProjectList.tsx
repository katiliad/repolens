import React, { useState, useEffect } from 'react';
import { List, ListItem, ListItemText, Button, Box, Dialog, DialogActions, DialogContent, DialogTitle, Chip } from '@mui/material';
import { getAllProjects, deleteProjectByName, getAuthorsByProjectName } from '../api';
import AuthorList from './AuthorList';
import ProjectForm from './ProjectForm';
import { Project } from '../api';

const ProjectList: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [message, setMessage] = useState<string>('');
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [projectToDelete, setProjectToDelete] = useState<string | null>(null);

  useEffect(() => {
    fetchProjects();
    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const fetchProjects = async () => {
    try {
      const response = await getAllProjects();
      if (response.error) {
        setMessage(response.error);
      } else {
        const projectsWithFlags = await Promise.all(
          response.data.map(async (project) => {
            const [platformResponse, devopsResponse] = await Promise.all([
              getAuthorsByProjectName(project.name, true, false),
              getAuthorsByProjectName(project.name, false, true),
            ]);

            const isPlatformEngineer = platformResponse.data.length > 0 && platformResponse.data[0].name !== "No authors found";
            const isDevopsEngineer = devopsResponse.data.length > 0 && devopsResponse.data[0].name !== "No authors found";

            return {
              ...project,
              isPlatformEngineer,
              isDevopsEngineer,
            };
          })
        );
        setProjects(projectsWithFlags);
      }
    } catch (error) {
      setMessage('An error occurred while fetching projects.');
    }
  };

  const handleDelete = (name: string) => {
    setProjectToDelete(name);
    setOpenDeleteDialog(true);
  };

  const confirmDelete = async () => {
    if (projectToDelete) {
      const response = await deleteProjectByName(projectToDelete);
      if (response.error) {
        setMessage(response.error);
      } else {
        setMessage('Project deleted successfully');
        fetchProjects(); 
      }
    }
    setOpenDeleteDialog(false);
  };

  const handleSelect = (project: Project) => {
    setSelectedProject(project);
  };

  const handleClickOutside = (event: MouseEvent) => {
    if (!(event.target as HTMLElement).closest('.scroll-container')) {
      resetState(); 
    }
  };

  const resetState = () => {
    setSelectedProject(null);
    setMessage('');
  };

  return (
    <Box>
      <ProjectForm onProjectCreated={fetchProjects} /> { }
      {message && <p>{message}</p>}
      <div className="scroll-container">
        <List>
          {projects.map((project) => (
            <ListItem key={project.name} button onClick={() => handleSelect(project)}>
              <ListItemText
                primary={project.name}
                secondary={project.url}
              />
              {project.isDevopsEngineer && (
                <Chip
                  label="DevOps"
                  color="success"
                  variant="outlined"
                  style={{ marginLeft: '8px' }}
                />
              )}
              {project.isPlatformEngineer && (
                <Chip
                  label="Platform Engineer"
                  color="info"
                  variant="outlined"
                  style={{ marginLeft: '8px' }}
                />
              )}
              <Button variant="contained" color="error" onClick={() => handleDelete(project.name)} style={{ marginLeft: '16px' }}>
                Delete
              </Button>
            </ListItem>
          ))}
        </List>
      </div>
      {selectedProject && <AuthorList projectName={selectedProject.name} />}

      {}
      <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <p>Are you sure you want to delete the project "{projectToDelete}"?</p>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDeleteDialog(false)}>No</Button>
          <Button onClick={confirmDelete} color="error">Yes</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ProjectList;
