import React from 'react';
import R from 'ramda';

const sortByColumn = (objects, { columnName, order }) => {
  if (columnName) {
    const prop = R.propOr(0, columnName);
    const sortedProp = (order === 'asc') ? R.ascend(prop) : R.descend(prop);
    const comparator = R.sort(sortedProp);

    return comparator(objects);
  } else {
    return objects;
  }
};

const TableHeaders = ({ columns, ordering, toggleOrdering }) => {
  return columns.map(([text, sortBy]) => {
    let title, icon;

    if (sortBy) {
      title = <a className="table-header" onClick={() => toggleOrdering(sortBy)}>{text}</a>;

      if (ordering.columnName === sortBy) {
        if (ordering.order === 'asc') {
          icon = <span className="glyphicon glyphicon-sort-by-attributes"></span>;
        } else {
          icon = <span className="glyphicon glyphicon-sort-by-attributes-alt"></span>;
        }
      }
    } else {
      title = text;
    }

    return (
      <th className="text-center" key={text}>
        {title}
        {' '}
        {icon}
      </th>
    );
  });
};

export { TableHeaders, sortByColumn };
