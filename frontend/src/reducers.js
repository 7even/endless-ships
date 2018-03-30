import { combineReducers } from 'redux';
import R from 'ramda';

const toggleFilter  = (filter = {}, value) => ({ ...filter, [value]: !filter[value] });
const initialFilter = (values) => R.uniq(R.flatten(values)).reduce(toggleFilter, {});

const toggleOrdering = (ordering, columnName) => {
  if (ordering.columnName === columnName) {
    if (ordering.order === 'asc') {
      return { columnName: null };
    } else {
      return { columnName, order: 'asc' };
    }
  } else {
    return { columnName, order: 'desc' };
  }
};

const isLoading         = (state = true, action) => (action.type === 'load-data') ? false : state;
const ships             = (state = [],   action) => (action.type === 'load-data') ? action.data.ships : state;
const shipModifications = (state = [],   action) => (action.type === 'load-data') ? action.data.shipModifications : state;
const outfits           = (state = [],   action) => (action.type === 'load-data') ? action.data.outfits : state;
const outfitters        = (state = [],   action) => (action.type === 'load-data') ? action.data.outfitters : state;

const filtersCollapsed = (state = true, action) => {
  if (action.type === 'toggle-ship-filters-visibility') {
    return !state;
  } else {
    return state;
  }
};

const shipsOrdering = (state = { columnName: null }, action) => {
  if (action.type === 'toggle-ships-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const raceFilter = (state = {}, action) => {
  switch (action.type) {
  case 'load-data':
    return initialFilter(action.data.ships.map(ship => ship.race));
  case 'toggle-ships-race-filtering':
    return toggleFilter(state, action.race);
  default:
    return state;
  }
};

const categoryFilter = (state = {}, action) => {
  switch (action.type) {
  case 'load-data':
    return initialFilter(action.data.ships.map(ship => ship.category));
  case 'toggle-ships-category-filtering':
    return toggleFilter(state, action.category);
  default:
    return state;
  }
};

const licenseFilter = (state = {}, action) => {
  switch (action.type) {
  case 'load-data':
    return initialFilter(action.data.ships.map(ship => ship.licenses));
  case 'toggle-ships-license-filtering':
    return toggleFilter(state, action.license);
  default:
    return state;
  }
};

const initialOrdering = columnName => ({ columnName, order: 'desc' });

const thrustersOrdering = (state = initialOrdering('Thrust per space'), action) => {
  if (action.type === 'toggle-thrusters-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const steeringsOrdering = (state = initialOrdering('Turn per space'), action) => {
  if (action.type === 'toggle-steerings-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const reactorsOrdering = (state = initialOrdering('Energy per space'), action) => {
  if (action.type === 'toggle-reactors-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const batteriesOrdering = (state = initialOrdering('Energy per space'), action) => {
  if (action.type === 'toggle-batteries-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const coolersOrdering = (state = initialOrdering('Cooling per space'), action) => {
  if (action.type === 'toggle-coolers-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const shieldGeneratorsOrdering = (state = initialOrdering('Shield per space'), action) => {
  if (action.type === 'toggle-shield-generators-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const hullRepairModulesOrdering = (state = initialOrdering('Hull per space'), action) => {
  if (action.type === 'toggle-hull-repair-modules-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const ramscoopsOrdering = (state = initialOrdering('Ramscoop per space'), action) => {
  if (action.type === 'toggle-ramscoops-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const gunsOrdering = (state = initialOrdering('Shield damage / space'), action) => {
  if (action.type === 'toggle-guns-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const secondaryWeaponsOrdering = (state = initialOrdering('Shield damage / space'), action) => {
  if (action.type === 'toggle-secondary-weapons-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const turretsOrdering = (state = initialOrdering('Shield damage / space'), action) => {
  if (action.type === 'toggle-turrets-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const antiMissileTurretsOrdering = (state = initialOrdering('Anti-missile'), action) => {
  if (action.type === 'toggle-anti-missile-turrets-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const handToHandWeaponsOrdering = (state = initialOrdering('Capture attack'), action) => {
  if (action.type === 'toggle-hand-to-hand-weapons-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

export default combineReducers({
  isLoading,
  ships,
  shipModifications,
  outfits,
  outfitters,
  shipSettings: combineReducers({
    filtersCollapsed,
    ordering: shipsOrdering,
    raceFilter,
    categoryFilter,
    licenseFilter
  }),
  outfitSettings: combineReducers({
    thrustersOrdering,
    steeringsOrdering,
    reactorsOrdering,
    batteriesOrdering,
    coolersOrdering,
    shieldGeneratorsOrdering,
    hullRepairModulesOrdering,
    ramscoopsOrdering,
    gunsOrdering,
    secondaryWeaponsOrdering,
    turretsOrdering,
    antiMissileTurretsOrdering,
    handToHandWeaponsOrdering
  })
});
