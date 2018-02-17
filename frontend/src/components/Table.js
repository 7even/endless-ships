import React from 'react';
import { Table as BSTable } from 'react-bootstrap';

import { TableHeaders } from '../ordering';

const Table = ({ headerColumns, ordering, toggleOrdering, children }) => {
  return (
    <BSTable striped bordered condensed hover>
      <thead>
        <tr>
          <TableHeaders columns={headerColumns}
                        ordering={ordering}
                        toggleOrdering={toggleOrdering} />
        </tr>
      </thead>
      <tbody>
        {children}
      </tbody>
    </BSTable>
  );
};

export default Table;
