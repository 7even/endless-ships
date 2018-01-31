import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { FormattedNumber } from './common';
import { Grid, Row, Col, Button, Collapse, Checkbox, Table } from 'react-bootstrap';
import R from 'ramda';

const kebabCase = string => string.replace(/\s+/g, '-').toLowerCase();

function LinkCell(props) {
  return (
    <td className="text-left">
      <Link to={`/ships/${kebabCase(props.text)}`}>{props.text}</Link>
    </td>
  );
}

function TextCell(props) {
  return (
    <td className="text-left">
      {props.text}
    </td>
  );
}

function RightCell(props) {
  return (
    <td className="text-right">
      {props.children}
    </td>
  );
}

function NumberCell(props) {
  return (
    <RightCell>
      <FormattedNumber number={props.number} />
    </RightCell>
  );
}

function CrewAndBunks(props) {
  if (props.crew > 0) {
    return (
      <RightCell>
        <FormattedNumber number={props.crew} />
        {' / '}
        <FormattedNumber number={props.bunks} />
      </RightCell>
    );
  } else {
    return (<RightCell></RightCell>);
  }
}

class ShipsList extends Component {
  capitalize([first, ...rest]) {
    return first.toUpperCase() + rest.join('').toLowerCase();
  }

  renderFilters() {
    const raceCheckboxes = this.props.races.map(race => (
      <Checkbox key={race}
                checked={this.props.raceFilter[race]}
                onChange={() => this.props.toggleRaceFiltering(race)}>
        {this.capitalize(race)}
      </Checkbox>
    ));

    const categoryCheckboxes = this.props.categories.map(category => (
      <Checkbox key={category}
                checked={this.props.categoryFilter[category]}
                onChange={() => this.props.toggleCategoryFiltering(category)}>
        {category}
      </Checkbox>
    ));

    const licenseCheckboxes = Object.keys(this.props.licenses).map(license => (
      <Checkbox key={license}
                checked={this.props.licenseFilter[license]}
                onChange={() => this.props.toggleLicenseFiltering(license)}>
        {license}
      </Checkbox>
    ));

    let collapseIcon;
    if (this.props.filtersCollapsed) {
      collapseIcon = <span className="glyphicon glyphicon-menu-down" />;
    } else {
      collapseIcon = <span className="glyphicon glyphicon-menu-up" />;
    }

    return (
      <div className="filters-group">
        <Collapse in={!this.props.filtersCollapsed}>
          <Grid fluid={true}>
            <Row>
              <Col lg={1}>
                <strong>Race</strong>
                {raceCheckboxes}
              </Col>
              <Col lg={1}>
                <strong>Category</strong>
                {categoryCheckboxes}
              </Col>
              <Col lg={2}>
                <strong>License</strong>
                {licenseCheckboxes}
              </Col>
            </Row>
          </Grid>
        </Collapse>
        <Button onClick={() => this.props.toggleFiltersVisibility()}>
          Filters {collapseIcon}
        </Button>
      </div>
    );
  }

  renderHeaders() {
    const columns = [
      ['Name', 'name'],
      ['Race'],
      ['Cost', 'cost'],
      ['Category'],
      ['Hull', 'hull'],
      ['Shields', 'shields'],
      ['Mass', 'mass'],
      ['Engine cap.', 'engineCapacity'],
      ['Weapon cap.', 'weaponCapacity'],
      ['Fuel cap.', 'fuelCapacity'],
      ['Outfit sp.', 'outfitSpace'],
      ['Cargo sp.', 'cargoSpace'],
      ['Crew / bunks', 'bunks'],
      ['Licenses']
    ];

    return columns.map(([text, sortBy]) => {
      let title, icon;

      if (sortBy) {
        title = <a className="table-header" onClick={() => this.props.toggleOrdering(sortBy)}>{text}</a>;

        if (this.props.ordering.columnName === sortBy) {
          if (this.props.ordering.order === 'asc') {
            icon = <span className="glyphicon glyphicon-sort-by-attributes"></span>;
          } else {
            icon = <span className="glyphicon glyphicon-sort-by-attributes-alt"></span>;
          }
        }
      } else {
        title = text;
      }

      return (
        <th className="text-center" key={text}>
          {title}
          {' '}
          {icon}
        </th>
      );
    });
  }

  renderLabel(text) {
    const style = this.props.licenses[text] || text;
    return (<span className={'label label-' + style} key={text}>{text}</span>);
  }

  renderLicenses(ship) {
    return ship.licenses.map(
      license => this.renderLabel(license)
    ).reduce(
      (licenses, license) => (licenses === null ? [license] : [...licenses, ' ', license]),
      null
    );
  }

  processedRows() {
    const filters = [
      ship => this.props.raceFilter[ship.race],
      ship => this.props.categoryFilter[ship.category],
      ship => R.none(license => !this.props.licenseFilter[license])(ship.licenses)
    ];

    const prop = R.propOr(0, this.props.ordering.columnName);
    const sortedProp = (this.props.ordering.order === 'asc') ? R.ascend(prop) : R.descend(prop);
    const comparator = R.sort(sortedProp);

    return comparator(R.filter(R.allPass(filters), this.props.ships));
  }

  renderRows() {
    return this.processedRows().map(ship => (
      <tr key={ship.name}>
        <LinkCell text={ship.name} />
        <TextCell text={this.renderLabel(ship.race)} />
        <NumberCell number={ship.cost} />
        <TextCell text={ship.category} />
        <NumberCell number={ship.hull} />
        <NumberCell number={ship.shields} />
        <NumberCell number={ship.mass} />
        <NumberCell number={ship.engineCapacity} />
        <NumberCell number={ship.weaponCapacity} />
        <NumberCell number={ship.fuelCapacity} />
        <NumberCell number={ship.outfitSpace} />
        <NumberCell number={ship.cargoSpace} />
        <CrewAndBunks crew={ship.requiredCrew} bunks={ship.bunks} />
        <TextCell text={this.renderLicenses(ship)} />
      </tr>
    ));
  }

  renderTable() {
    return (
      <Table striped bordered condensed hover>
        <thead>
          <tr>
            {this.renderHeaders()}
          </tr>
        </thead>
        <tbody>
          {this.renderRows()}
        </tbody>
      </Table>
    );
  }

  render() {
    return (
      <div className="app">
        <ul className="nav nav-tabs">
          <li role="presentation" className="active">
            <Link to="/">Ships</Link>
          </li>
          <li role="presentation">
            <Link to="/outfits">Outfits</Link>
          </li>
        </ul>

        <ol className="breadcrumb top20"><li className="active">Ships</li></ol>

        {this.renderFilters()}
        {this.renderTable()}
      </div>
    );
  }
}

export default ShipsList;
