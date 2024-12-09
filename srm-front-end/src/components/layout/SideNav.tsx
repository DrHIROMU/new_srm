import { NavLink } from "react-router";

import classes from "./SideNav.module.scss";

const SideNav = () => {
  return (
    <nav className={classes["side-nav"]}>
      <ul>
        <li>
          <NavLink to="/home">Home</NavLink>
        </li>
        <li>
          <NavLink to="/vendor-info">Vendor Info</NavLink>
        </li>
      </ul>
    </nav>
  );
};

export default SideNav;
