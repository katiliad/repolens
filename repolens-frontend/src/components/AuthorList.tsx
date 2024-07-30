import React, { useState, useEffect } from 'react';
import { List, ListItem, ListItemText, Box } from '@mui/material';
import { getAuthorsByProjectName, AuthorInfo } from '../api';
import FileChanges from './FileChanges';

interface AuthorListProps {
  projectName: string;
}

const AuthorList: React.FC<AuthorListProps> = ({ projectName }) => {
  const [authors, setAuthors] = useState<AuthorInfo[]>([]);
  const [selectedAuthor, setSelectedAuthor] = useState<AuthorInfo | null>(null);
  const [message, setMessage] = useState<string>('');

  useEffect(() => {
    fetchAuthors();
  }, [projectName]);

  const fetchAuthors = async () => {
    const response = await getAuthorsByProjectName(projectName);
    if (response.error) {
      setMessage(response.error);
    } else {
      setAuthors(response.data);
    }
  };

  const handleSelect = (author: AuthorInfo) => {
    setSelectedAuthor(author);
  };

  return (
    <Box>
      <h2>Authors in {projectName}</h2>
      {message && <p>{message}</p>}
      <div className="scroll-container">
        <List>
          {authors.map((author) => (
            <ListItem key={author.name} button onClick={() => handleSelect(author)}>
              <ListItemText primary={author.name} secondary={`Platform: ${author.platformEngineer}, DevOps: ${author.devopsEngineer}`} />
            </ListItem>
          ))}
        </List>
      </div>
      {selectedAuthor && <FileChanges projectName={projectName} authorName={selectedAuthor.name} />}
    </Box>
  );
}

export default AuthorList;
