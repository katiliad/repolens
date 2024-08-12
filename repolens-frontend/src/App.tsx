import React from 'react';
import { Container, Typography } from '@mui/material';
import ProjectList from './components/ProjectList';

const App: React.FC = () => {
  return (
    <Container>
      <Typography variant="h2" gutterBottom>
        RepoLens
      </Typography>
      <ProjectList />
    </Container>
  );
}

export default App;
