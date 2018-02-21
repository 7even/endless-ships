import React from 'react';
import R from 'ramda';

const sortByColumn = (objects, columnProps, { columnName, order }) => {
  if (columnName) {
    const prop       = columnProps.get(columnName);
    const sortedProp = (order === 'asc') ? R.ascend(prop) : R.descend(prop);
    const comparator = R.sort(sortedProp);

    return comparator(objects);
  } else {
    return objects;
  }
};

const TableHeaders = ({ columns, ordering, toggleOrdering }) => {
  return Array.from(columns).map(([text, prop]) => {
    let title, icon;

    if (prop) {
      title = <a className="table-header" onClick={() => toggleOrdering(text)}>{text}</a>;

      if (ordering.columnName === text) {
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
