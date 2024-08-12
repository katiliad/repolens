import axios, { AxiosError } from 'axios';

const API_URL = 'http://localhost:8080';

export interface Project {
  name: string;
  url: string;
  isDevopsEngineer : boolean;
  isPlatformEngineer: boolean;
}

export interface AuthorInfo {
  name: string;
  platformEngineer: boolean;
  devopsEngineer: boolean;
  commitCount: number;
}

export interface FileChangeInfo {
  extension: string;
  count: number;
}

export const getAllProjects = async () : Promise<any> => {
  try {
    return await axios.get<Project[]>(`${API_URL}/`);
  } catch (error) {
    return handleAxiosError(error as AxiosError);
  }
};

export const getProjectByName = async (name: string) : Promise<any> => {
  try {
    return await axios.get<Project>(`${API_URL}/${name}`);
  } catch (error) {
    return handleAxiosError(error as AxiosError);
  }
};

export const createProject = async (url: string, name: string) : Promise<any> => {
  try {
    return await axios.post(`${API_URL}/create`, null, { params: { url, name } });
  } catch (error) {
    return handleAxiosError(error as AxiosError);
  }
};

export const deleteProjectByName = async (name: string) : Promise<any> => {
  try {
    return await axios.delete(`${API_URL}/${name}`);
  } catch (error) {
    return handleAxiosError(error as AxiosError);
  }
};

export const getAuthorsByProjectName = async (name: string, platformEng?: boolean, devopsEng?: boolean) : Promise<any> => {
  try {
    return await axios.get<AuthorInfo[]>(`${API_URL}/${name}/authors`, { params: { platformEng, devopsEng } });
  } catch (error) {
    return handleAxiosError(error as AxiosError);
  }
};

export const getFileChangesByProjectAndAuthor = async (project: string, author: string) : Promise<any> => {
  try {
    return await axios.get<FileChangeInfo[]>(`${API_URL}/${project}/changedFiles/${author}`);
  } catch (error) {
    return handleAxiosError(error as AxiosError);
  }
};

const handleAxiosError = (error: AxiosError) => {
  if (error.response) {
    return { error: error.response.data };
  }
  return { error: 'An unknown error occurred' };
};
