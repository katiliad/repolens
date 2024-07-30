import React, { useState } from 'react';
import { TextField, Button, Box } from '@mui/material';
import { createProject } from '../api';

interface ProjectFormProps {
  onProjectCreated: () => void; // Callback prop to trigger refetch
}

const ProjectForm: React.FC<ProjectFormProps> = ({ onProjectCreated }) => {
  const [url, setUrl] = useState<string>('');
  const [name, setName] = useState<string>('');
  const [message, setMessage] = useState<string>('');
  const [nameError, setNameError] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setNameError('Project Name is mandatory');
      return;
    }
    setNameError('');
    
    const response = await createProject(url, name);
    if (response.error) {
      setMessage(response.error);
    } else {
      setMessage('Project created successfully');
      onProjectCreated(); // Refetch projects
      setUrl('');
      setName('');
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
      <TextField
        label="GitHub URL"
        variant="outlined"
        fullWidth
        margin="normal"
        value={url}
        onChange={(e) => setUrl(e.target.value)}
      />
      <TextField
        label="Project Name"
        variant="outlined"
        fullWidth
        margin="normal"
        value={name}
        onChange={(e) => setName(e.target.value)}
        error={!!nameError}
        helperText={nameError}
      />
      <Button variant="contained" color="primary" type="submit">
        Create Project
      </Button>
      {message && <p>{message}</p>}
    </Box>
  );
};

export default ProjectForm;
