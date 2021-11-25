import React, { useCallback } from "react";
import { Table } from "react-bootstrap";
import { useHttp } from "../hooks/useHttp.hook";
import { DeleteButton } from "./button";

const marginTopStyle = {
  marginTop: "10px"
};

const StatCardList = ({ pools }) => {
  const { loading, request } = useHttp();

  const deletePool = useCallback(
    async id => {
      try {
        const data = await request(`http://localhost:3000/${id}`, "DELETE");
      } catch (e) {
        console.log(e);
      }
    },
    [request]
  );

  if (pools.length === 0) {
    return <p className="center">Статистики пока нет</p>;
  }

  return (
    <Table striped bordered hover style={marginTopStyle}>
      <thead>
        <tr>
          <th>Название</th>
          <th>Ссылка</th>
          <th>На весь университет</th>
          <th>Факультеты</th>
          <th>Группы</th>
          <th>Дата начала</th>
          <th>Дата окончания</th>
          <th></th>
        </tr>
      </thead>

      <tbody>
        {pools.map((pool, index) => {
          const formatDateStart = pool.dateStart.substring(0, 10);
          const formatDateEnd = pool.dateEnd.substring(0, 10);

          return (
            <tr key={pool.id}>
              <td>{pool.name}</td>
              <td>
                <a href={pool.link} target="_blank" rel="noopener noreferrer">
                  Открыть
                </a>
              </td>
              <td>{pool.allStudents.length === 1 ? "Да" : "Нет"}</td>
              <td>{pool.faculties.map(v => v.name).join(" ")}</td>
              <td>{pool.groups.map(v => v.name).join(" ")}</td>
              <td>{formatDateStart}</td>
              <td>{formatDateEnd}</td>
              <td>
                <DeleteButton
                  deletePool={deletePool}
                  formatDateEnd={formatDateEnd}
                  formatDateStart={formatDateStart}
                  poolId={pool.id}
                />
              </td>
            </tr>
          );
        })}
      </tbody>
    </Table>
  );
};

export default StatCardList;
