/* eslint-disable react/prop-types */
import { useState } from 'react';

export default function Item(
    { id,
        name,
        finished,
        // eslint-disable-next-line no-unused-vars
        due,
        updateFinished,
        deleteTodo,
        updateName }
) {
    const [edit, setEdit] = useState(false);
    const [editName, setEditName] = useState(name);

    return (
        <div className="item">
            <div className="circle" onClick={() => updateFinished(id)}>
                {finished ? <span>&#10003;</span> : ""}
            </div>
            <div
                className={finished ? "strike" : ""}
                onDoubleClick={() => {
                    if (!finished) {
                        setEdit(true);
                    }
                }}
            >
                {edit ? (
                    <input
                        type="text"
                        value={editName}
                        onChange={(e) => {
                            setEditName(e.target.value);
                        }}
                        onBlur={() => {
                            setEdit(false);
                            updateName(id, editName);
                        }}
                    />
                ) : (name)}
            </div>
            <div className="close" onClick={() => deleteTodo(id)}>
                X
            </div>
        </div>
    );
};