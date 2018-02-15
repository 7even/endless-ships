import React from 'react';
import { Link } from 'react-router-dom';

import { FormattedNumber, kebabCase, nbsp, nbspize, renderLicenses } from '../common';

const LinkCell = ({ text }) => (
  <td className="text-left">
    <Link to={`/ships/${kebabCase(text)}`}>{text}</Link>
  </td>
);

const TextCell = ({ text }) => (
  <td className="text-left">{text}</td>
);

const RightCell = ({ children }) => (
  <td className="text-right">{children}</td>
);

const NumberCell = ({ number }) => (
  <RightCell>
    <FormattedNumber number={number} />
  </RightCell>
);

const CrewAndBunks = ({ crew, bunks }) => {
  if (crew > 0) {
    return (
      <RightCell>
        <FormattedNumber number={crew} />
        {nbsp + '/' + nbsp}
        <FormattedNumber number={bunks} />
      </RightCell>
    );
  } else {
    return (<RightCell />);
  }
};

const renderLabel = (text, style = text) => (
  <span className={'label label-' + style} key={text}>{text}</span>
);

const ShipRow = ({ ship }) => (
  <tr>
    <LinkCell text={nbspize(ship.name)} />
    <TextCell text={renderLabel(ship.race)} />
    <NumberCell number={ship.cost} />
    <TextCell text={nbspize(ship.category)} />
    <NumberCell number={ship.hull} />
    <NumberCell number={ship.shields} />
    <NumberCell number={ship.mass} />
    <NumberCell number={ship.engineCapacity} />
    <NumberCell number={ship.weaponCapacity} />
    <NumberCell number={ship.fuelCapacity} />
    <NumberCell number={ship.outfitSpace} />
    <NumberCell number={ship.cargoSpace} />
    <CrewAndBunks crew={ship.requiredCrew} bunks={ship.bunks} />
    <TextCell text={renderLicenses(ship.licenses)} />
  </tr>
);

export default ShipRow;
