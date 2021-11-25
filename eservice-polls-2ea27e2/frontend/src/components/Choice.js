import { Multiselect } from "multiselect-react-dropdown";
import { React, useState } from "react";
import { Form } from "react-bootstrap";

const marginTopStyle = {
  marginTop: "10px"
};

function Choice({
  setFaculty,
  setGroup,
  faculties,
  groups,
  isDisabled,
  setState
}) {
  const facultiesOptions = [
    { name: "ФКН", id: 1 },
    { name: "ИМИТ", id: 2 }
  ];
  const groupsOptions = [
    { name: "СПБ-701", id: 1 },
    { name: "СИБ-901", id: 2 }
  ];
  const [facultiesData, setFacultiesData] = useState(facultiesOptions);
  const [groupsData, setGroupsData] = useState(groupsOptions);

  const [facultiesCache, setFacultiesCache] = useState([]);
  const [groupsCache, setGroupsCache] = useState([]);

  const onSelectFaculty = data => setFaculty(data);
  const onRemoveFaculty = data => setFaculty(data);
  const onSelectGroup = data => setGroup(data);
  const onRemoveGroup = data => setGroup(data);

  const changeToDisabled = event => {
    setFaculty([]);
    setGroup([]);

    setState(!isDisabled);
    setFacultiesCache(faculties);
    setGroupsCache(groups);

    if (isDisabled === true) setFaculty(facultiesCache);
    if (isDisabled === true) setGroup(groupsCache);
  };

  return (
    <>
      <div class="row" class="text-center" style={marginTopStyle}>
        <div class="row">
          <div class="col-sm-2">
            <Form.Check
              label="Отправить всему университету"
              onChange={changeToDisabled}
            />
          </div>
          <div class="col-sm-5">
            <Multiselect
              placeholder="Укажите факультеты"
              emptyRecordMsg="Вы указали все факультеты"
              disable={isDisabled}
              options={facultiesData}
              selectedValues={faculties}
              displayValue="name"
              onSelect={onSelectFaculty}
              onRemove={onRemoveFaculty}
            />
          </div>
          <div class="col-sm-5">
            <Multiselect
              placeholder="Укажите группы"
              emptyRecordMsg="Вы указали все группы"
              disable={isDisabled}
              options={groupsData}
              selectedValues={groups}
              displayValue="name"
              onSelect={onSelectGroup}
              onRemove={onRemoveGroup}
            />
          </div>
        </div>
      </div>
    </>
  );
}

export default Choice;
