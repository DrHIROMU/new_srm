import { FormEvent } from "react";

const LoginPage = () => {
  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const form = event.target as HTMLFormElement;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    console.log(data);
  }

  return (
    <>
      <form onSubmit={handleSubmit}>
        <div>
          <div>
            <label htmlFor="email">Email</label>
            <input id="email" name="email" type="text" />
          </div>
          <div>
            <label htmlFor="password">Password</label>
            <input id="password" name="password" type="password" />
          </div>
        </div>

        <button type="submit">Login</button>
      </form>
    </>
  );
};

export default LoginPage;
