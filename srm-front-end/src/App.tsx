import { BrowserRouter, Routes, Route } from "react-router";

import "./App.scss";
import LoginPage from "./pages/login/Login";
import HomePage from "./pages/home/Home";
import Layout from "./components/layout/Layout";
import VendorInfoPage from "./pages/vendor/VendorInfo";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route path="/home" element={<HomePage />} />
          <Route path="/vendor-info" element={<VendorInfoPage />} />
        </Route>
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
