import axios from "axios";

export const searchApi = axios.create({
  baseURL: "http://localhost:8086/search"
});
