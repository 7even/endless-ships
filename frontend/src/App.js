import React, { Component } from 'react';
import endlessShips from './reducers';
import { createStore } from 'redux';
import { Provider } from 'react-redux';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import { Grid, Row, Col } from 'react-bootstrap';
import R from 'ramda';

import Reference from './components/Reference';
import ShipsList from './components/ShipsList';
import ShipPage from './components/ShipPage';
import OutfitsList from './OutfitsList';

import 'bootstrap/dist/css/bootstrap.css';
import './App.css';

class App extends Component {
  kebabCase = string => string.replace(/\s+/g, '-').toLowerCase();

  // TODO: compute this from this.state.ships on the fly
  races = [
    'human',
    'hai',
    'quarg',
    'korath',
    'wanderer',
    'coalition',
    'pug',
    'pirate',
    'remnant',
    'drak'
  ];

  categories = [
    'Transport',
    'Light Freighter',
    'Heavy Freighter',
    'Interceptor',
    'Light Warship',
    'Medium Warship',
    'Heavy Warship',
    'Fighter',
    'Drone'
  ];

  licenses = {
    'Navy':               'human',
    'Carrier':            'human',
    'Cruiser':            'human',
    'Militia Carrier':    'human',
    'Unfettered Militia': 'hai',
    'Wanderer':           'wanderer',
    'Wanderer Military':  'wanderer',
    'Coalition':          'coalition',
    'Heliarch':           'coalition',
    'Remnant':            'remnant'
  };

  state = {
    isLoading: true,
    data: {},
    filtersCollapsed: true,
    ordering: { columnName: null },
    raceFilter: this.races.reduce(
      (races, race) => R.merge(races, { [race]: true }),
      {}
    ),
    categoryFilter: this.categories.reduce(
      (categories, category) => R.merge(categories, { [category]: true }),
      {}
    ),
    licenseFilter: Object.keys(this.licenses).reduce(
      (licenses, license) => R.merge(licenses, { [license]: true }),
      {}
    )
  }

  componentDidMount() {
    window.fetch('/data.json').then(response => {
      return response.json();
    }).then(data => {
      this.setState({ isLoading: false, ships: data });
    });
  }

  toggleFiltersVisibility = () => {
    this.setState({ filtersCollapsed: !this.state.filtersCollapsed });
  };

  toggleRaceFiltering = (race) => {
    this.setState({
      raceFilter: R.merge(
        this.state.raceFilter,
        { [race]: !this.state.raceFilter[race] }
      )
    });
  };

  toggleCategoryFiltering = (category) => {
    this.setState({
      categoryFilter: R.merge(
        this.state.categoryFilter,
        { [category]: !this.state.categoryFilter[category] }
      )
    });
  }

  toggleLicenseFiltering = (license) => {
    this.setState({
      licenseFilter: R.merge(
        this.state.licenseFilter,
        { [license]: !this.state.licenseFilter[license] }
      )
    });
  }

  toggleOrdering = (columnName) => {
    if (this.state.ordering.columnName === columnName) {
      if (this.state.ordering.order === 'asc') {
        this.setState({ ordering: { columnName: null } });
      } else {
        this.setState({ ordering: { columnName: columnName, order: 'asc' } });
      }
    } else {
      this.setState({ ordering: { columnName: columnName, order: 'desc' } });
    }
  }

  render() {
    if (this.state.isLoading) {
      return (<div className="app">Loading...</div>);
    } else {
      return (
        <Router>
          <Grid fluid={true}>
            <Row>
              <Col lg={12}>
                <Route exact={true} path="/" render={props => (
                  <ShipsList ships={this.state.ships}
                             races={this.races}
                             categories={this.categories}
                             licenses={this.licenses}
                             filtersCollapsed={this.state.filtersCollapsed}
                             ordering={this.state.ordering}
                             raceFilter={this.state.raceFilter}
                             categoryFilter={this.state.categoryFilter}
                             licenseFilter={this.state.licenseFilter}
                             toggleFiltersVisibility={this.toggleFiltersVisibility}
                             toggleRaceFiltering={this.toggleRaceFiltering}
                             toggleCategoryFiltering={this.toggleCategoryFiltering}
                             toggleLicenseFiltering={this.toggleLicenseFiltering}
                             toggleOrdering={this.toggleOrdering} />
                )}/>
                <Route path="/ships/:shipName" render={({ match }) => (
                  <ShipPage ship={this.state.ships.find(ship => this.kebabCase(ship.name) === match.params.shipName)}/>
                )}/>
                <Route path="/outfits" component={OutfitsList} />
              </Col>
            </Row>
          </Grid>
        </Router>
      );
    }
  }
}

class App2 extends Component {
  render() {
    return (
      <Provider store={createStore(endlessShips)}>
        <div className="app">
          <Reference>
            <Router>
              <Grid fluid={true}>
                <Row>
                  <Col lg={12}>
                    <Route exact={true} path="/" component={ShipsList} />
                    <Route path="/ships/:shipName" component={ShipPage} />
                  </Col>
                </Row>
              </Grid>
            </Router>
          </Reference>
        </div>
      </Provider>
    );
  }
};

export default App2;
