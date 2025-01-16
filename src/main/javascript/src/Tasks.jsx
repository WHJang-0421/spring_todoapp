/* eslint-disable react/prop-types */
import { useState, useRef, useEffect } from 'react';
import axios from 'axios';

import Item from './Item'

export default function Tasks() {
  const [todos, setTodos] = useState([]);
  const [username, setUsername] = useState('');
  const inputRef = useRef();

  useEffect(() => {
    fetchTodos()
  }, []);
  function fetchTodos() {
    axios.get('/api/task', {
      withCredentials: true
    })
      .then((response) => {
        setTodos(response.data);
      })
      .catch((error) => {
        console.log("Error while fetching tasks:", error);
      });
  }

  useEffect(() => {
    axios.get('/api/user', { withCredentials: true })
      .then((response) => {
        setUsername(response.data.username);
      })
      .catch((error) => {
        console.log(error);

      });
  });

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      const text = e.target.value;
      inputRef.current.value = "";

      axios.post('/api/task', {
        name: text,
        finished: false,
        due: "2022-12-31"
      }, {
        withCredentials: true
      })
        .then((response) => {
          let item = response.data;
          item.key = item.id;
          setTodos([
            ...todos,
            item
          ])
        })
        .catch((error) => {
          console.log("Error while fetching tasks:", error);
        });
    }
  };

  // toggle finished
  const handleFinished = (id) => {
    const updatedList = todos.map((e) => {
      if (e.id === id) {
        e.finished = !e.finished;
      }

      return e;
    });
    setTodos(updatedList);

    const item = todos.filter((e) => e.id == id)[0];

    axios.put(`/api/task/${id}`, {
      name: item.name,
      finished: item.finished,
      due: item.due
    }, {
      withCredentials: true
    });
  };

  // delete item
  const handleDelete = (id) => {
    const filter = todos.filter((e) => e.id !== id);
    setTodos(filter);
    axios.delete(`/api/task/${id}`)
      .catch((error) => {
        console.log("Error while fetching tasks:", error);
      }, {
        withCredentials: true
      });
  };

  // handle text update
  const handleUpdateName = (id, name) => {
    const updatedList = todos.map((e) => {
      if (e.id === id) {
        e.name = name;
      }

      return e;
    });
    setTodos(updatedList);

    const item = todos.filter((e) => e.id == id)[0];

    axios.put(`/api/task/${id}`, {
      name: item.name,
      finished: item.finished,
      due: item.due
    }, {
      withCredentials: true
    });
  };

  return (
    <div className="App">
      Welcome, {username}!
      <input type="text" onKeyUp={handleKeyPress} ref={inputRef} />
      {todos.map((e) =>
      (<Item
        {...e}
        key={e.id}
        updateFinished={handleFinished}
        deleteTodo={handleDelete}
        updateName={handleUpdateName}
      />)
      )}
    </div>
  );
}