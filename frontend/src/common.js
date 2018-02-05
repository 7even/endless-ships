import React from 'react';
import NumberFormat from 'react-number-format';

const kebabCase = string => string.replace(/\s+/g, '-').toLowerCase();

const FormattedNumber = ({ number }) => {
  return (
    <NumberFormat value={number}
                  displayType={'text'}
                  thousandSeparator={true} />
  );
};

export { FormattedNumber, kebabCase };
