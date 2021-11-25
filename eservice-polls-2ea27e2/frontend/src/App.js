import React, { useState, useEffect, useCallback } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-datepicker/dist/react-datepicker.css';
import DatePicker from 'react-datepicker';
import { useHttp } from './hooks/useHttp.hook.js';
import {
  Form,
  Button,
  Container,
  Navbar,
  NavDropdown,
  Nav,
} from 'react-bootstrap';
import Choice from './components/Choice.js';
import Spinner from 'react-bootstrap/Spinner';
import StatCardList from './components/StatCardList';

const marginTopStyle = {
  marginTop: '10px',
};

const marginRightStyle = {
  marginRight: '10px',
};

function App(callback, deps) {
  const { loading, request } = useHttp();
  const [dateRange, setDateRange] = useState([null, null]);
  const [dateStart, dateEnd] = dateRange;

  const [form, setForm] = useState({});

  const [faculties, setFaculty] = useState([]);
  const [groups, setGroup] = useState([]);
  const [isDisabled, setState] = useState(false);

  const [pools, setPools] = useState([]);

  const changeHandler = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const getPools = useCallback(async () => {
    try {
      const data = await request('http://localhost:3000/', 'GET');
      setPools(data);
    } catch (e) {
      console.log(e);
    }
  }, [request, setPools]);

  const saveForm = useCallback(async () => {
    try {
      const data = await request('http://localhost:3000/', 'POST', {
        ...form,
        dateEnd,
        dateStart,
        allStudents: isDisabled,
        faculties: faculties.map((v) => v.name),
        groups: groups.map((v) => v.name),
      });
    } catch (e) {
      console.log(e);
    }
  }, [request, groups, faculties, dateStart, dateEnd, form]);

  useEffect(() => {
    getPools();
  }, [getPools]);

  return (
    <>
      <Container>
        <Navbar collapseOnSelect expand="lg" bg="" variant="white">
          <Container>
            <Navbar.Brand style={{ color: 'green' }} href="#home">
              Создание опроса
            </Navbar.Brand>
            <Navbar.Toggle aria-controls="responsive-navbar-nav" />
            <Navbar.Collapse id="responsive-navbar-nav">
              <Nav className="me-auto"></Nav>
              <Nav>
                <Nav.Link style={{ color: 'green' }} href="#сервисы">
                  Все сервисы
                </Nav.Link>
                <Nav.Link eventKey={2} style={{ color: 'green' }} href="#Выйти">
                  Выйти
                </Nav.Link>
              </Nav>
            </Navbar.Collapse>
          </Container>
        </Navbar>

        <hr
          style={{
            backgroundColor: 'green',
            height: 2,
          }}
        />

        <Form>
          <Form.Group className="mb-3" style={marginTopStyle}>
            <h6>Название опроса</h6>
            <Form.Control
              name="name"
              onChange={changeHandler}
              placeholder="Укажите название опроса"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <h6>Ссылка на опрос</h6>
            <Form.Control
              onChange={changeHandler}
              name="link"
              type="link"
              placeholder="Укажите ссылку на опрос"
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <h6>Даты проведения опроса</h6>
            <DatePicker
              selectsRange={true}
              startDate={dateStart}
              endDate={dateEnd}
              onChange={(update) => {
                setDateRange(update);
              }}
              minDate={Date.now()}
              placeholderText="Укажите даты"
              isClearable={true}
            />
          </Form.Group>

          <Choice
            faculties={faculties}
            groups={groups}
            setFaculty={setFaculty}
            setGroup={setGroup}
            isDisabled={isDisabled}
            setState={setState}
          />
          <Button
            variant="primary"
            type="confirm"
            style={marginTopStyle}
            onClick={saveForm}
          >
            Добавить опрос
          </Button>

          <StatCardList pools={pools} style={marginTopStyle} />
        </Form>
      </Container>
    </>
  );
}

export default App;
