import app from "./app";

const PORT = Number(process.env.PORT);

if (!PORT) {
  throw new Error("PORT is not defined");
}

app.listen(PORT, "0.0.0.0", () => {
  console.log("Server running on port " + PORT);
});
