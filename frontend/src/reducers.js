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

const isLoading = (state = true, action) => (action.type === 'load-data') ? false : state;
const ships     = (state = [],   action) => (action.type === 'load-data') ? action.data.ships : state;
const outfits   = (state = [],   action) => (action.type === 'load-data') ? action.data.outfits : state;

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

const thrustersOrdering = (state = { columnName: 'Thrust', order: 'desc' }, action) => {
  if (action.type === 'toggle-thrusters-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const steeringsOrdering = (state = { columnName: 'Turn', order: 'desc' }, action) => {
  if (action.type === 'toggle-steerings-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const reactorsOrdering = (state = { columnName: 'Energy generation', order: 'desc' }, action) => {
  if (action.type === 'toggle-reactors-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const batteriesOrdering = (state = { columnName: 'Energy capacity', order: 'desc' }, action) => {
  if (action.type === 'toggle-batteries-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const coolersOrdering = (state = { columnName: 'Cooling', order: 'desc' }, action) => {
  if (action.type === 'toggle-coolers-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const shieldGeneratorsOrdering = (state = { columnName: 'Shield generation', order: 'desc' }, action) => {
  if (action.type === 'toggle-shield-generators-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

const hullRepairModulesOrdering = (state = { columnName: 'Hull repair rate', order: 'desc' }, action) => {
  if (action.type === 'toggle-hull-repair-modules-ordering') {
    return toggleOrdering(state, action.columnName);
  } else {
    return state;
  }
};

export default combineReducers({
  isLoading,
  ships,
  outfits,
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
    hullRepairModulesOrdering
  })
});
