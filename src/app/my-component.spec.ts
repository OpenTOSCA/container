import { NgReduxTestingModule, MockNgRedux } from '@angular-redux/store/testing';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/toArray';

import { AppComponent } from './app.component';
import { IAppState } from '../store';
import { CounterActions } from './app.actions';

describe('AppComponent', () => {
  beforeEach(() => {
    // Configure your testBed to use NgReduxTestingModule; this test the DI
    // in the test environment to use mock versions of NgRedux and DevToolsExtension.
    TestBed.configureTestingModule({
      declarations: [AppComponent],
      imports: [NgReduxTestingModule],
      providers: [CounterActions]
    }).compileComponents();

    // Reset the mock to start from a clean slate in each unit test.
    MockNgRedux.reset();
  });

  it('dispatches INCREMENT when user press ' + ' button', () => {
    // Create an instance of MyComponent using Angular's normal unit test features.
    const fixture = TestBed.createComponent(AppComponent);
    const componentUnderTest = fixture.debugElement.componentInstance;

    // Get a stub we can use to drive the `@select('count')` observable used by
    // MyComponent (above). This stub will be supplied to any relevant `.select`
    // or `@select` calls used by the component under test by MockNgRedux.
    const countStub: Subject<number> = MockNgRedux.getSelectorStub<IAppState, number>('count');

    // Determine a sequence of values we'd like to test the Redux store with.
    const expectedValues = [ 1, 2, 3, 4, 3, 4, 3, 2, 1];
    const spy = spyOn(MockNgRedux.mockInstance, 'dispatch');
    expect(spy).toHaveBeenCalledWith({type: CounterActions.INCREMENT});
    // Drive those values through our stub.
    expectedValues.forEach(value => countStub.next(value));

    // Make sure MyComponent's selected count$ variable receives these values.
    componentUnderTest.count$
      .toArray()
      .subscribe(
        actualValues => expect(actualValues).toEqual(expectedValues),
        null,
        done);
  });
