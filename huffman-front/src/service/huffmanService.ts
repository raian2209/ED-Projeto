import axios from "axios";

const API_URL = "http://localhost:8080";

export const api = axios.create({
  baseURL: API_URL,
});

export const compressFile = async (file: File) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await api.post("/huffman/compress", formData, {
    responseType: "blob",
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return response.data;
};

export const decompressFile = async (file: File) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await api.post("/huffman/decompress", formData, {
    responseType: "blob",
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
  return response.data;
};
