import {getBothToken, getToken, removeBothToken, removeToken, setBothToken} from "./localStorageService";
import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";

export const logIn = async (username, password) => {
  const response = await httpClient.post(API.LOGIN, {
    username: username,
    password: password,
  });

  setBothToken(response.data?.result);

  return response;
};

export const logOut = async () => {
  const bothToken = getBothToken();
  await httpClient.post(API.LOGOUT, {
    tokenAccess: bothToken.accessToken,
    tokenRefresh: bothToken.refreshToken,
  });
  removeBothToken();
};

export const isAuthenticated = () => {
  return getToken();
};
