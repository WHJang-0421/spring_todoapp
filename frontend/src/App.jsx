import './App.css'
import axios from 'axios'
import { useEffect } from 'react'

function App() {
  useEffect(() => {
    axios.get("/api/hi")
      .then((result) => {
        console.log(result.data);
      })
      .catch((error) => {
        console.log(error);
      })
  });
  return (
    <>
    </>
  )
}

export default App
