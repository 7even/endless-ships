import React from 'react';

import ThrustersTable from './outfits/ThrustersTable';
import SteeringsTable from './outfits/SteeringsTable';
import ReactorsTable from './outfits/ReactorsTable';
import BatteriesTable from './outfits/BatteriesTable';
import CoolersTable from './outfits/CoolersTable';

const OutfitsList = () => (
  <div className="app">
    <h2>Thrusters</h2>
    <ThrustersTable />

    <h2>Steerings</h2>
    <SteeringsTable />

    <h2>Reactors</h2>
    <ReactorsTable />

    <h2>Batteries</h2>
    <BatteriesTable />

    <h2>Coolers</h2>
    <CoolersTable />
  </div>
);

export default OutfitsList;
