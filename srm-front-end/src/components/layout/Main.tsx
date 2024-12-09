import { Outlet } from "react-router";

import classes from "./Main.module.scss";

const MainContent = () => {
  return (
    <main className={classes.main}>
      <Outlet />
    </main>
  );
};

export default MainContent;
