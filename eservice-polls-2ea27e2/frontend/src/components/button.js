import { Button } from "react-bootstrap";
import React from "react";
import { compareAsc } from "date-fns";

export const DeleteButton = ({
  formatDateStart,
  formatDateEnd,
  deletePool,
  poolId
}) => {
  return (
    <div>
      {compareAsc(new Date(), new Date(formatDateEnd)) === 1 ? (
        " "
      ) : (
        <Button
          variant="danger"
          type="confirm"
          onClick={() => deletePool(poolId)}
        >
          {compareAsc(new Date(), new Date(formatDateStart)) === 1
            ? "Отменить"
            : "Удалить"}
        </Button>
      )}
    </div>
  );
};
