import React, { useState, useEffect } from 'react';
import { List, ListItem, ListItemText, Button, Box } from '@mui/material';
import { getAllProjects, deleteProjectByName } from '../api';
import AuthorList from './AuthorList';
import { Project } from '../api';

const ProjectList: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [message, setMessage] = useState<string>('');

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    const response = await getAllProjects();
    if (response.error) {
      setMessage(response.error);
    } else {
      setProjects(response.data);
    }
  };

  const handleDelete = async (name: string) => {
    const response = await deleteProjectByName(name);
    if (response.error) {
      setMessage(response.error);
    } else {
      setMessage('Project deleted successfully');
      fetchProjects();
    }
  };

  const handleSelect = (project: Project) => {
    setSelectedProject(project);
  };

  return (
    <Box>
      {message && <p>{message}</p>}
      <div className="scroll-container">
        <List>
          {projects.map((project) => (
            <ListItem key={project.name} button onClick={() => handleSelect(project)}>
              <ListItemText primary={project.name} secondary={project.url} />
              <Button variant="contained" color="secondary" onClick={() => handleDelete(project.name)}>
                Delete
              </Button>
            </ListItem>
          ))}
        </List>
      </div>
      {selectedProject && <AuthorList projectName={selectedProject.name} />}
    </Box>
  );
}

export default ProjectList;
