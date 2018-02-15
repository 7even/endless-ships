import React from 'react';
import NumberFormat from 'react-number-format';
import R from 'ramda';

import LicenseLabel from './components/LicenseLabel';

const kebabCase = string => string.replace(/\s+/g, '-').toLowerCase();

const capitalize = ([first, ...rest]) => first.toUpperCase() + rest.join('').toLowerCase();

const nbsp = '\u00a0';

const nbspize = string => string.replace(/ /g, nbsp);

const FormattedNumber = ({ number }) => {
  return (
    <NumberFormat value={number}
                  displayType={'text'}
                  thousandSeparator={true} />
  );
};

const renderLicenses = (licenses) => {
  const labels = licenses.map(license => <LicenseLabel license={license} key={license} />);

  return R.intersperse(' ', labels);
};

export { FormattedNumber, kebabCase, capitalize, nbsp, nbspize, renderLicenses };
