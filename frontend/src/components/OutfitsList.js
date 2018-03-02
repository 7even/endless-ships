import React from 'react';

import ThrustersTable from './outfits/ThrustersTable';
import SteeringsTable from './outfits/SteeringsTable';
import ReactorsTable from './outfits/ReactorsTable';
import BatteriesTable from './outfits/BatteriesTable';
import CoolersTable from './outfits/CoolersTable';
import ShieldGeneratorsTable from './outfits/ShieldsTable';
import HullRepairModulesTable from './outfits/HullRepairModules';
import RamscoopsTable from './outfits/RamscoopsTable';
import GunsTable from './outfits/GunsTable';
import SecondaryWeaponsTable from './outfits/SecondaryWeaponsTable';
import TurretsTable from './outfits/TurretsTable';
import AntiMissileTurretsTable from './outfits/AntiMissileTurretsTable';
import HandToHandWeaponsTable from './outfits/HandToHandWeaponsTable';

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

    <h2>Shield generators</h2>
    <ShieldGeneratorsTable />

    <h2>Hull repair modules</h2>
    <HullRepairModulesTable />

    <h2>Ramscoops</h2>
    <RamscoopsTable />

    <h2>Guns</h2>
    <GunsTable />

    <h2>Secondary weapons</h2>
    <SecondaryWeaponsTable />

    <h2>Turrets</h2>
    <TurretsTable />

    <h2>Anti-missile turrets</h2>
    <AntiMissileTurretsTable />

    <h2>Hand to Hand</h2>
    <HandToHandWeaponsTable />
  </div>
);

export default OutfitsList;
