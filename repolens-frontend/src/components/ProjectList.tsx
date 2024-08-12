import React, { useState, useEffect } from 'react';
import { List, ListItem, ListItemText, Button, Box, Dialog, DialogActions, DialogContent, DialogTitle, Chip, CircularProgress } from '@mui/material';
import { getAllProjects, deleteProjectByName, getAuthorsByProjectName } from '../api';
import AuthorList from './AuthorList';
import ProjectForm from './ProjectForm';
import { Project } from '../api';
import axios from 'axios';

const ProjectList: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [message, setMessage] = useState<string>('');
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [projectToDelete, setProjectToDelete] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (!(event.target as HTMLElement).closest('.scroll-container')) {
        resetState(); 
      }
    };

    fetchProjects();
    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const fetchProjects = async () => {
    setLoading(true);
    try {
      const response = await getAllProjects();
      const projects = response.data;

      const projectsWithFlags = await Promise.all(
        projects.map(async (project : Project) => {
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
      setLoading(false);
    } catch (error) {
      setLoading(false);
      if (axios.isAxiosError(error)) {
        setMessage(error.response?.data.error || 'An error occurred while fetching projects.');
      } else {
        setMessage('An unexpected error occurred.');
      }
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

  const resetState = () => {
    setSelectedProject(null);
    setMessage('');
  };

  return (
    <Box position="relative" minHeight="100vh">
      <ProjectForm onProjectCreated={fetchProjects} setLoading={setLoading} /> 
      {message && <p>{message}</p>}
      
      {loading && (
        <Box 
          position="fixed" 
          top="0" 
          left="0" 
          right="0" 
          bottom="0" 
          display="flex" 
          alignItems="center" 
          justifyContent="center" 
          bgcolor="rgba(255, 255, 255, 0.7)" 
          zIndex="1000"
        >
          <CircularProgress />
        </Box>
      )}

      {!loading && (
        <div className="scroll-container">
          <List>
            {projects.map((project : Project) => (
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
      )}

      {selectedProject && <AuthorList projectName={selectedProject.name} />}

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
