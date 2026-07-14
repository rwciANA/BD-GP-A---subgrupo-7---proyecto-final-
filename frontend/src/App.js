import React, { useEffect, useState } from 'react';
import axios from 'axios';

function App() {
  const [tablas, setTablas] = useState([]);
  const [tablaSeleccionada, setTablaSeleccionada] = useState('');
  const [filas, setFilas] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    cargarTablas();
  }, []);

  const cargarTablas = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/db/tables');
      setTablas(res.data);
      if (res.data.length > 0) {
        setTablaSeleccionada(res.data[0].table_name);
        cargarTabla(res.data[0].table_name);
      }
    } catch (e) {
      setError('No se pudo conectar al backend. Revisa que Spring Boot esté corriendo.');
    }
  };

  const cargarTabla = async (nombre) => {
    try {
      const res = await axios.get(`http://localhost:8080/api/db/table/${nombre}`);
      setFilas(res.data);
      setError('');
    } catch (e) {
      setError('No se pudo consultar la tabla.');
    }
  };

  return (
    <div style={{ padding: 24, fontFamily: 'Arial' }}>
      <h1>Cooperativa de Ahorros</h1>
      <h2>Visualizador de tablas PostgreSQL</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <select
        value={tablaSeleccionada}
        onChange={(e) => {
          setTablaSeleccionada(e.target.value);
          cargarTabla(e.target.value);
        }}
        style={{ marginBottom: 16, padding: 8 }}
      >
        {tablas.map((t) => (
          <option key={t.table_name} value={t.table_name}>{t.table_name}</option>
        ))}
      </select>

      <div style={{ overflowX: 'auto' }}>
        <table border="1" cellPadding="8" cellSpacing="0">
          <thead>
            <tr>
              {filas.length > 0 && Object.keys(filas[0]).map((col) => <th key={col}>{col}</th>)}
            </tr>
          </thead>
          <tbody>
            {filas.map((fila, i) => (
              <tr key={i}>
                {Object.values(fila).map((valor, j) => <td key={j}>{valor ?? ''}</td>)}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default App;
