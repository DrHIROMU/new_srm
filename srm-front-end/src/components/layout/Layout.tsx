import Header from "./Header";
import SideNav from "./SideNav";
import MainContent from "./Main";
import classes from "./Layout.module.scss";

const Layout = () => {
  return (
    <div className={classes.layout}>
      <Header />
      <div className={classes.body}>
        <SideNav />
        <MainContent />
      </div>
    </div>
  );
};

export default Layout;
