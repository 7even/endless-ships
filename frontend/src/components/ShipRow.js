import React from 'react';

import { FormattedNumber, nbsp, nbspize, renderLicenses, ShipLink } from '../common';
import { TextCell, RightCell, NumberCell } from './Table';

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
    <TextCell><ShipLink ship={ship} /></TextCell>
    <TextCell>{renderLabel(ship.race)}</TextCell>
    <NumberCell number={ship.cost} />
    <TextCell>{nbspize(ship.category)}</TextCell>
    <NumberCell number={ship.hull} />
    <NumberCell number={ship.shields} />
    <NumberCell number={ship.mass} />
    <NumberCell number={ship.engineCapacity} />
    <NumberCell number={ship.weaponCapacity} />
    <NumberCell number={ship.fuelCapacity} />
    <NumberCell number={ship.outfitSpace} />
    <NumberCell number={ship.cargoSpace} />
    <CrewAndBunks crew={ship.requiredCrew} bunks={ship.bunks} />
    <TextCell>{renderLicenses(ship.licenses)}</TextCell>
  </tr>
);

export default ShipRow;
