import React from 'react';
import { Table as BSTable } from 'react-bootstrap';

import { FormattedNumber, floatFormatter } from '../common';
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

const TextCell = ({ children }) => (
  <td className="text-left">{children}</td>
);

const RightCell = ({ children }) => (
  <td className="text-right">{children}</td>
);

const NumberCell = ({ number }) => (
  <td className="text-right">
    {number && <FormattedNumber number={number} isDecimal={false} />}
  </td>
);

const DecimalCell = ({ decimal }) => (
  <td className="text-right">
    {decimal && <FormattedNumber number={floatFormatter.format(decimal)} isDecimal={true} />}
  </td>
);

export default Table;
export { TextCell, RightCell, NumberCell, DecimalCell };
