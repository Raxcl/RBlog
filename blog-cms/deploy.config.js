module.exports = {
  projectName: "blog-cms",
  privateKey: "C:\\Users\\Administrator\\Desktop\\secrty",
  passphrase: "",
  cluster: [],
  prod: {
    name: "生产环境",
    script: "yarn build",
    host: "81.71.87.241",
    port: 22,
    username: "root",
    password: "",
    distPath: "dist",
    webDir: "/mnt/raxcl/blog_admin",
    isRemoveRemoteFile: true,
    isRemoveLocalFile: true,
  },
};
