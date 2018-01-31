import React from 'react';
import NumberFormat from 'react-number-format';

const FormattedNumber = ({ number }) => {
  return (
    <NumberFormat value={number}
                  displayType={'text'}
                  thousandSeparator={true} />
  );
};

export { FormattedNumber };
