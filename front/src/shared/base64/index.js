export const base64 = async (file) => {
    const reader = new FileReader();
    return new Promise((resolve, reject) => {
        reader.onloadend = () => {
            const fileContent = reader.result;
            resolve(fileContent);
        };
        reader.onerror = () => {
            reject(new Error("Oops, something went wrong with the file reader."));
        };
        reader.readAsDataURL(file);
    });
};
