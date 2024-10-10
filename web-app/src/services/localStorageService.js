export const KEY_TOKEN_ACCESS = "accessToken";
export const KEY_TOKEN_REFRESH = "refreshToken"

export const setBothToken = (bothToken) => {
    localStorage.setItem(KEY_TOKEN_ACCESS, bothToken.tokenAccess);
    localStorage.setItem(KEY_TOKEN_REFRESH, bothToken.tokenRefresh);
};

export const getBothToken = () => {
    let accessToken = localStorage.getItem(KEY_TOKEN_ACCESS);
    let refreshToken = localStorage.getItem(KEY_TOKEN_REFRESH);
    return {accessToken, refreshToken}
};

export const removeBothToken = () => {
    localStorage.removeItem(KEY_TOKEN_ACCESS);
    localStorage.removeItem(KEY_TOKEN_REFRESH);
};

export const getToken = () => {
    return localStorage.getItem(KEY_TOKEN_ACCESS);
};

export const removeToken = () => {
    return localStorage.removeItem(KEY_TOKEN_ACCESS);
};
