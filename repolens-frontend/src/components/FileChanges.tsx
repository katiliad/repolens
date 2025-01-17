import React, { useState, useEffect } from 'react';
import { List, ListItem, ListItemText, Box } from '@mui/material';
import { getFileChangesByProjectAndAuthor, FileChangeInfo } from '../api';

interface FileChangesProps {
  projectName: string;
  authorName: string;
}

const FileChanges: React.FC<FileChangesProps> = ({ projectName, authorName }) => {
  const [fileChanges, setFileChanges] = useState<FileChangeInfo[]>([]);
  const [message, setMessage] = useState<string>('');

  useEffect(() => {
    const fetchFileChanges = async () => {
      const response = await getFileChangesByProjectAndAuthor(projectName, authorName);
      if (response.error) {
        setMessage(response.error);
      } else {
        setFileChanges(response.data);
      }
    };

    fetchFileChanges();
  }, [projectName, authorName]);

  return (
    <Box>
      <h2>File Changes by {authorName}</h2>
      {message && <p>{message}</p>}
      <div className="scroll-container">
        <List>
          {fileChanges.map((fileChange, index) => (
            <ListItem key={index}>
              <ListItemText
                primary={fileChange.extension || "Unknown file type"} 
                secondary={`Changes: ${fileChange.count}`}
              />
            </ListItem>
          ))}
        </List>
      </div>
    </Box>
  );
}

export default FileChanges;
