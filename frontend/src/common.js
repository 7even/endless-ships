import React from 'react';
import NumberFormat from 'react-number-format';
import { Link } from 'react-router-dom';
import R from 'ramda';

import LicenseLabel from './components/LicenseLabel';

const kebabCase = string => string.replace(/\s+/g, '-').toLowerCase();

const capitalize = ([first, ...rest]) => first.toUpperCase() + rest.join('').toLowerCase();

const nbsp = '\u00a0';

const nbspize = string => string.replace(/ /g, nbsp);

const FormattedNumber = ({ number, isDecimal }) => {
  return (
    <NumberFormat value={number}
                  displayType={'text'}
                  thousandSeparator={true}
                  decimalScale={2}
                  fixedDecimalScale={isDecimal} />
  );
};

const renderLicenses = (licenses = []) => {
  const labels = licenses.map(license => <LicenseLabel license={license} key={license} />);

  return R.intersperse(' ', labels);
};

const ShipLink = ({ shipName }) => (
  <Link to={`/ships/${kebabCase(shipName)}`}>{nbspize(shipName)}</Link>
);

const OutfitLink = ({ outfitName }) => (
  <Link to={`/outfits/${kebabCase(outfitName)}`}>{nbspize(outfitName)}</Link>
);

export { FormattedNumber, kebabCase, capitalize, nbsp, nbspize, renderLicenses, ShipLink, OutfitLink };
